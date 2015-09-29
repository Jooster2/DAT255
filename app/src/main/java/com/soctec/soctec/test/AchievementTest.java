package com.soctec.soctec.test;

import android.test.InstrumentationTestCase;

import com.soctec.soctec.AchievementCreator;
import com.soctec.soctec.AchievementUnlocker;
import com.soctec.soctec.MainActivity;
import com.soctec.soctec.Stats;
/**
 * Created by Carl-Henrik Hult on 2015-09-29.
 */
public class AchievementTest extends InstrumentationTestCase
{
    MainActivity main = new MainActivity();
    Stats stats = new Stats();
    AchievementCreator creator = new AchievementCreator (main);
    AchievementUnlocker unlocker = new AchievementUnlocker (stats, creator);

    /**
     * Tests if its possible to create an Achievement by checking the unlockedAchievements-list after
     * trying to create one.
     * @throws Exception
     */
    public void testCreateAchievement () throws Exception
    {
        creator.addObserver(unlocker);
        unlocker.invokeTestAchievement();
        assertEquals( 1, unlocker.getUnlockableAchievements().size());
    }

    /**
     * Checks if the scancount gets increased when a scan har occured (A fake scan)
     * @throws Exception
     */
    public void testScanCountInc () throws Exception
    {
        creator.addObserver(unlocker);
        unlocker.invokeTestAchievement();
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(stats.getScanCount(), 1);

    }

    /**
     * Checks if the LastScanned-String object is the same as the one received from the "fake" scan.
     * @throws Exception
     */
    public void testLastScanned () throws Exception
    {
        creator.addObserver(unlocker);
        unlocker.invokeTestAchievement();
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals("JOCKE", stats.getlastScanned());
    }

    /**
     * Checks if a list containing one achievement gets emptied when that achievement gets unlocked.
     * @throws Exception
     */
    public void testUnlockAchievement () throws Exception
    {
        creator.addObserver(unlocker);
        unlocker.invokeTestAchievement();
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(0, unlocker.getUnlockableAchievements().size());
    }
}
