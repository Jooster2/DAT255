import threading
import socket
import sqlite3
import json


def runFunction(clientSocket, address):
    package = clientSocket.recv(1024)
    hexcode, command, data = package.decode().split(':')
    hexcode = hexcode.strip()
    command = command.strip()
    data = data.strip().split(',')
    if(command == '0'):
        dbWrite(hexcode, data)
    elif(command == '1'):
        scanAttempt(hexcode, data)
    elif(command == '2'):
        dbRead(hexcode)
    else:
        clientSocket.send(('Error, bad command specified: ' + command).encode())

def dbWrite(hexcode, data):
    db = sqlite3.connect('soctec-db')
    cursor = db.cursor()
    tupleList = list(zip(['ipAddress', 'points', 'ratingPos', 'ratingNeg'], data))
    del data[:4]
    tupleList.append(('achievements', json.dumps(data)))
    for item in tupleList:
        if item[1] != '-1':
            print(item[0], type(item[1]))
            cursor.execute('''UPDATE users SET {}=? WHERE userCode=?'''.format(item[0]),
                    (item[1], hexcode))
    db.commit()
    db.close()




def scanAttempt(hexcode, data):
    pass




def dbRead(hexcode):
    db = sqlite3.connect('soctec-db')
    cursor = db.cursor()
    cursor.execute('''SELECT * FROM users WHERE userCode=?''', (hexcode,))
    asd = cursor.fetchone()
    print(type(asd[0]))



if __name__ == '__main__':
    # Init database, create if non-existant
    db = sqlite3.connect('soctec-db')
    cursor = db.cursor()
    # Create Table for database
    try:
        cursor.execute('''CREATE TABLE users(
            userCode TEXT PRIMARY KEY,
            ipAddress TEXT,
            points TEXT,
            ratingPos TEXT,
            ratingNeg TEXT,
            achievements TEXT)''')
        db.commit()
        print('Created Table: users')
    except:
        pass
    db.close()
            
    serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serverSocket.bind(('localhost', 49998))
    serverSocket.listen(5)

    while True:
        clientSocket, address = serverSocket.accept()
        clientThread = threading.Thread(target=runFunction(clientSocket, address))
        clientThread.start()

