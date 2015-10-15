package com.soctec.soctec.test;

import android.test.AndroidTestCase;

import com.soctec.soctec.achievements.AchievementCreator;
import com.soctec.soctec.achievements.AchievementUnlocker;
import com.soctec.soctec.core.MainActivity;
import com.soctec.soctec.achievements.Stats;
/**
 * Tests for different types of Achievements
 * @author Carl-Henrik Hult, Joakim Schmidt
 * @version 1.4
 */
public class AchievementTest extends AndroidTestCase
{
    MainActivity main = new MainActivity();
    Stats stats = new Stats();
    AchievementCreator creator = new AchievementCreator();
    AchievementUnlocker unlocker = new AchievementUnlocker(main, stats, creator);

    /**
     * Tests if its possible to create an Achievement by checking the unlockedAchievements-list after
     * trying to create one.
     * @throws Exception
     */
    public void testCreateAchievement() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        assertEquals( 1, unlocker.getUnlockableAchievements().size());
    }

    /**
     * Checks if the scancount gets increased when a scan has occured (A fake scan)
     * @throws Exception
     */
    public void testScanCountInc() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(stats.getScanCount(), 1);

    }

    /**
     * Checks if the LastScanned-String object is the same as the one received from the "fake" scan.
     * @throws Exception
     */
    public void testLastScanned() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals("JOCKE", stats.getlastScanned());
    }
    /**
     * Checks if a list containing one achievement gets emptied when that achievement gets unlocked.
     * @throws Exception
     */
    public void testUnlockAchievement() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(0, unlocker.getUnlockableAchievements().size());
    }


    /**
     * Creates four fake achievements and checks if they end up in the unlockedAchievements-list.
     * @throws Exception
     */
    public void testCreate4Achievements() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        creator.createTestAch("First Scan!, flavorText, 50, someimg, S1, SIN, P_SCAN:1");
        assertEquals(4, unlocker.getUnlockableAchievements().size());

        //TODO : Write more tests, what kind of tests?
    }

    /**
     * Tests the functionality of Infinite Achievements, both re-creation and increase in demand
     * @throws Exception
     */
    public void testCreateInfiniteAchievements() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("GOGO! Towards Infinity!, flavorText, 25, gold_trophy, SI0, INF, P_SCAN:0:2^c");
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals("SI0", stats.getAchievements().get(0).getId());
        assertEquals(1, unlocker.getUnlockableAchievements().size());
        // Check that the new achievements goal is set correctly according to equation
        // Should be 2 (current total of scans is 1)
        assertEquals("2", unlocker.getUnlockableAchievements().get(0).getDemands().get(0).requirement);
        unlocker.receiveEvent(1, "JOCKE");
        // Should be 4 (current total of scans is 2)
        assertEquals("4", unlocker.getUnlockableAchievements().get(0).getDemands().get(0).requirement);
        unlocker.receiveEvent(1, "JOCKE");
        unlocker.receiveEvent(1, "JOCKE");
        // Should be 8 (current total of scans is 4)
        assertEquals("8", unlocker.getUnlockableAchievements().get(0).getDemands().get(0).requirement);
    }

    /**
     * Tests the functionality of an infinite "time talked" achievement
     * Requires tweak in AchievementUnlocker.receiveEvent to work, or can be tested with
     * Thread.sleep and double receiveEvent: event, sleep(2.5s), event again (content can be null
     * if this is tried)
     * @throws Exception
     */
    public void testTimeTalkedAchievements() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("MaratonSnackaren!, Du har pratat, 25, gold_trophy, TI0, INF, T_TALK:1:a*2");
        unlocker.receiveEvent(4, "6");
        assertEquals("TI0", stats.getAchievements().get(0).getId());
    }

    /**
     * Tests the functionality on the "do-not-allow-multiple-consecutive-scans-on-the-same-person"
     * functionality in Stats. Requires tweaking of the value on line 213 in Stats,
     * set it to something below 1000.
     * @throws Exception
     */
    public void testDoubleScanNegated() throws Exception
    {
        creator.addObserver(unlocker);
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(1, stats.getScanCount());
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(1, stats.getScanCount());
        try{ Thread.sleep(2500); } catch(InterruptedException e) {e.printStackTrace(); }
        stats.removeOldScans();
        assertEquals(false, stats.isScannedRecently("JOCKE"));
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(2, stats.getScanCount());
    }

    /**
     * Tests SIN-Achievements, with T_TIME-Demands. Double receiveEvent calls are for recursivity
     * which is how it is supposed to happen naturally. Stats.setLongestTalkStreak() must be
     * tweaked for test to work.
     * @throws Exception
     */
    public void testLongestTalkStreak() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("Långa möten, Du har pratat 5min med samma person, 50, gold_ribbon, S3, SIN, T_TALK:300");
        unlocker.receiveEvent(1, "JOCKE");
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals(0, stats.getAchievements().size());
        stats.setLongestTalkStreak(400);
        unlocker.receiveEvent(1, "JOCKE");
        assertEquals("S3", stats.getAchievements().get(0).getId());
    }

    /**
     * Tests the functionality of CollectionAchievements.
     * Requires tweak in AchievementCreator.createAchievement to work, see comments in code there.
     * @throws Exception
     */
    public void testCollectionAchievements() throws Exception
    {
        creator.addObserver(unlocker);
        creator.createTestAch("Pure Electric!, You have gone on all three electric buses, "+
                "30, gold_trophy, C1, COL, B_RIDE:E_BUS1/E_BUS2/E_BUS3");
        assertEquals(3, unlocker.getUnlockableAchievements().get(0).getDemands().size());
        unlocker.receiveEvent(2, "E_BUS1");
        unlocker.receiveEvent(2, "E_BUS3");
        assertEquals(1, unlocker.getUnlockableAchievements().get(0).getDemands().size());
        unlocker.receiveEvent(2, "E_BUS2");
        assertEquals(0, unlocker.getUnlockableAchievements().size());
        assertEquals(1, stats.getAchievements().size());
    }
}
