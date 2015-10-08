#!/usr/bin/python
import threading
import socket
import sqlite3

dbLock = threading.Lock()

def socketInputThread(clientSocket, address):
    # Temporary protocol, needs MUCH work
    packet = clientSocket.recv(1024)
    # Data could be empty, so we do try and handle both cases
    try:
        userCode, command, data = packet.decode().split(':')
        userCode = userCode.strip()
        command = command.strip()
        # Makes a list of the data
    except ValueError as e:
        userCode, command = packet.decode().split(':')
        userCode = userCode.strip()
        command = command.strip()
    if(command == '0'):
        data = data.strip().split(',')
        dbWrite(userCode, data)
    elif(command == '1'):
        scanAttempt(userCode, data)
    elif(command == '2'):
        """ We read the database and get a tuple back
            The tuple is made into a list for better manipulation-tools
            We take the first items from the list (the non-BLOB items)
            Then we delete those and extract the letter from the BLOB
            by checking if each char in the BLOB is alphanumerical
        """
        fromDB = dbRead(userCode)
        fromDB = list(fromDB)
        try:
            answerPacket = []
            for x in fromDB[:5]:
                answerPacket.append(x)
            answerPacket.extend(fromDB[5].split(','))

            # Convert the list to a string with a ',' between each letter
            answerPacket = ','.join(answerPacket)
            clientSocket.send(answerPacket.encode())
        except:
            clientSocket.send(('Some error occurred, data found was:', ''.join(fromDB)).encode())
    else:
        clientSocket.send(('Error, bad command specified: ' + command).encode())
    # Socket should be closed after use because there is really no reason
    # to keep it open, right?
    clientSocket.close()

def dbWrite(userCode, data):
    # Thread lock is used to protect the database from multiple writes
    dbLock.acquire()
    db = sqlite3.connect('soctec-db')
    cursor = db.cursor()

    # Pairs the data with respective column in the database
    tupleList = list(zip(['ipAddress', 'points', 'ratingPos', 'ratingNeg'], data))
    # Delete the already paired data
    del data[:4]
    # The rest is achievements. JSON is used to create a sqlite3-BLOB for writing
    data = list(data)
    tupleList.append(('achievements', ','.join(data)))

    # We try to create, PRIMARY KEY prevents recreating if exists
    try:
        cursor.execute('''INSERT INTO users(userCode) VALUES(?)''', (userCode,))
    except:
        pass
    for item in tupleList:
        # -1 is temporary "no change made" token (to reduce bandwidth)
        if item[1] != '-1': 
            cursor.execute('''UPDATE users SET {}=? WHERE userCode=?'''.format(item[0]),
                    (item[1], userCode))
    db.commit()
    db.close()
    print('Data received')
    dbLock.release()




def scanAttempt(userCode, data):
    clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    clientSocket.connect((userCode, 49999))
    clientSocket.send(data.encode())
    clientSocket.close()

def dbRead(userCode):
    db = sqlite3.connect('soctec-db')
    cursor = db.cursor()
    cursor.execute('''SELECT * FROM users WHERE userCode=?''', (userCode,))
    return cursor.fetchone()

if __name__ == '__main__':
    # Init database, create if non-existant
    dbLock.acquire()
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
    dbLock.release()
            
    serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serverSocket.bind(('', 49999))
    serverSocket.listen(5)

    while True:
        clientSocket, address = serverSocket.accept()
        clientThread = threading.Thread(target=socketInputThread(clientSocket, address))
        clientThread.start()

