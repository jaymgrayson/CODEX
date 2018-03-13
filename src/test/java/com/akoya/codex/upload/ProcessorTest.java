package com.akoya.codex.upload;

import com.akoya.codex.MicroscopeTypeEnum;
import com.akoya.codex.TestHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.annotations.AfterTest;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Scanner;

public class ProcessorTest {

    private static frmMain frm;
    private static ExperimentView expView;
    private static ProcessingOptionsView poView;
    private static Experiment exp;
    private static ProcessingOptions po;

    private String testExp = "D:\\exp2Test";
    private String outDir = "D:\\exp2TestPro";

    @BeforeTest
    public void setUp() throws Exception {
        frm = new frmMain();
        expView = frm.getExperimentView();

        JTextField txtDir = new JTextField();
        txtDir.setText(testExp);
        expView.setTxtDir(txtDir);

        File expJS = new File(testExp + File.separator + "Experiment.json");
        if (expJS.exists()) {
            try {
                expView.load(Experiment.loadFromJSON(expJS), new File(testExp));
            } catch (Exception e) {
                logger.showException(e);
            }
        }

        exp = expView.getExperiment();

        poView = frm.getUploadOptionsView();

        JTextField txtTempDir = new JTextField();
        txtTempDir.setText(outDir);
        poView.setTxtTempDir(txtTempDir);

        File poFile = new File(testExp + File.separator + "processingOptions.json");
        po = ProcessingOptions.load(poFile);
    }

    @Test(priority = 1)
    public void testFrmMainFields() throws Exception {
        Assert.assertNotNull(frm);
        Assert.assertNotNull(exp);
        Assert.assertNotNull(expView);
        Assert.assertNotNull(poView);
        Assert.assertNotNull(po);
        Assert.assertNotNull(expView.getTxtDir());

        //Microscope type val
        Assert.assertNotNull(expView.getVal3());
        Assert.assertEquals(MicroscopeTypeEnum.KEYENCE.equals(expView.getVal3().getSelectedItem()) || MicroscopeTypeEnum.ZEISS.equals(expView.getVal3().getSelectedItem()), true);

        //Tile overlap percentage
        Assert.assertNotNull(expView.getVal19());
        Assert.assertNotNull(expView.getVal20());
    }

    @Test(priority = 2)
    public void testChannelNames() throws Exception {
        File channelNamesFile = new File(testExp + File.separator + "channelNames.txt");
        Assert.assertEquals(true, channelNamesFile.exists() && channelNamesFile.isFile());
    }

    @Test(priority = 3)
    public void testFrmMain() throws Exception {
        File frmMainTestRunFile = new File(outDir+File.separator+"frmMainTestLog.txt");
        PrintStream p = new PrintStream(frmMainTestRunFile);
        System.setOut(p);
        System.setErr(p);

        int sec =0;
        Thread th= frm.cmdStartActionPerformed(new ActionEvent(this, 1, "TestEvt"));
        do {
            Thread.currentThread().sleep(1000);
        }while(th.isAlive() && (sec++) < 600);


        Assert.assertTrue(sec < 600);
        Assert.assertTrue(frmMainTestRunFile.exists());
    }

    @Test(priority = 4)
    public void testLogFileForErrors() throws Exception {
        File frmMainTestRunFile = new File(outDir+File.separator+"frmMainTestLog.txt");
        Scanner scanner = new Scanner(frmMainTestRunFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Assert.assertFalse(line.toLowerCase().contains("error") || line.toLowerCase().contains("exception"));
        }
    }

    @Test(priority = 5)
    public void testForBestFocus() throws Exception {
        File bestFocusFile = new File(outDir + File.separator + "bestFocus");
        Assert.assertTrue(bestFocusFile.exists() && bestFocusFile.isDirectory());
    }

    @Test(priority = 6)
    public void testMakeMontage() throws Exception {
        File bestFocusdir = new File(outDir + File.separator + "bestFocus");
        File[] montageFiles = bestFocusdir.listFiles(m -> m.getName().toLowerCase().contains("montage.tif"));
        Assert.assertTrue(montageFiles.length != 0);
    }

    @Test(priority = 7)
    public void testTileMap() throws Exception {
        File tileMapFile = new File(outDir + File.separator + "tileMap.txt");
        Assert.assertTrue(tileMapFile.exists() && tileMapFile.isFile());
    }
}