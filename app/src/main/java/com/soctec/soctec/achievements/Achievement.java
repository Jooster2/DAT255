package com.soctec.soctec.achievements;

/**
 * Created by Carl-Henrik Hult on 2015-09-22.
 * An abstract superclass for all achievements.
 */
public abstract class Achievement
{
    private String name;
    private int points;
    private String imageName;
    private String id;
    private String type;

    /**
     *
     * @param name  The name of the achievement.
     * @param points    The points that the achievement is worth.
     * @param imageName The filename/name of the image that goes with the achievement.
     * @param id    An internal ID
     * @param type the type of the achievement (SIN/INF)
     */
    public Achievement (String name, int points,String imageName, String id, String type)
    {
        this.name = name;
        this.points = points;
        this.imageName = imageName;
        this.id = id;
        this.type = type;
    }

    /**
     * Returns the name of the achievement.
     * @return  name of the achievement.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the points that the achievement is worth.
     * @return  points that the achievement is worth.
     */
    public int getPoints()
    {
        return points;
    }

    /**
     * Returns the filename/name of the image that goes with the achievement.
     * @return filename/name of the image that goes with the achievement.
     */
     public String getImageName()
    {
        return imageName;
    }

    /**
     * Returns the internal ID for the achievement.
     * @return  internal ID for the achievement.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the type of the Achievement (SIN/INF)
     * @return the type of the Achievement (SIN/INF)
     */
    public String getType()
    {
        return type;
    }

    /**
     * Returns an Array containing basic information used to build this Achievement
     * @return an Array containing basic information used to build this Achievement
     */
    public String[] getAllData()
    {
        String[] data = {name, String.valueOf(points), imageName, id, type};
        return data;
    }

}
