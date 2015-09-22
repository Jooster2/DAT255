package com.soctec.soctec;

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

    /**
     *
     * @param name  The name of the achievement.
     * @param points    The points that the achievement is worth.
     * @param imageName The filename/name of the image that goes with the achievement.
     * @param id    An internal ID
     */
    public Achievement (String name, int points,String imageName, String id)
    {
        this.name = name;
        this.points = points;
        this.imageName = imageName;
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public int getPoints()
    {
        return points;
    }
     public String getImageName()
    {
        return imageName;
    }
    public String getId()
    {
        return id;
    }

}
