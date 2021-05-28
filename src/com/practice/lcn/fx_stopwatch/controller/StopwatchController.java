package com.practice.lcn.fx_stopwatch.controller;

import javafx.scene.control.Label;
import javafx.application.Platform;

import org.apache.commons.lang.time.StopWatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stopwatch inner workings.
 *
 * @author lcn
 */
public class StopwatchController {
    private static Logger logger = LogManager.getLogger(StopwatchController.class.getName());

    /**
     * stopwatch state
     */
    public enum STATE {
        /**
         * indicating that stopwatch is currently running
         */
        RUNNING,
        /**
         * indicating that stopwatch is paused temporarily
         */
        PAUSE,
        /**
         * indicating that stopwatch is reset
         */
        STOP
    };
    /**
     * stopwatch <b>hh:mm:ss.SSS</b> GUI display's refresh interval (in ms)
     */
    private static final long UPDATE_DISPLAY_INTERVAL = 50;
    /**
     * JavaFX task to update the stopwatch GUI display
     * @return JavaFX task
     */
    private Runnable taskUpdateDisplay = new Runnable() {
        @Override
        public void run() {
            updateDisplay();
        }
    };
    /**
     * initial <b>hh:mm:ss</b> in stopwatch GUI display
     */
    public static final String INIT_HHMMSS = "00:00:00";
    /**
     * initial <b>SSS</b> in stopwatch GUI display
     */
    public static final String INIT_SSS = "000";

    /**
     * <b>hh:mm:ss</b> label in stopwatch GUI display
     */
    private Label labelHhmmss;
    /**
     * <b>SSS</b> label in stopwatch GUI display
     */
    private Label labelSss;

    /**
     * stopwatch's current state
     */
    private volatile STATE state;
    /**
     * internal stopwatch
     */
    private StopWatch sw;
    /**
     * stopwatch main thread
     */
    private Thread tStopwatch;

    /**
     * initialization
     */
    public StopwatchController() {
        this.tStopwatch = null;
        this.state = STATE.STOP;
        this.sw = new StopWatch();
    }

    /**
     * start the stopwatch. Meanwhile, stopwatch GUI display will be updated.
     */
    public void start() {
        this.tStopwatch = new Thread(new Runnable() {
            @Override
            public void run() {
                if (state == STATE.STOP) {
                    sw.start();
                }
                else if (state == STATE.PAUSE) {
                    sw.resume();
                }
                state = STATE.RUNNING;

                while (state == STATE.RUNNING) {
                    sw.suspend();
                    Platform.runLater(taskUpdateDisplay);
                    sw.resume();

                    try {
                        Thread.sleep(StopwatchController.UPDATE_DISPLAY_INTERVAL);
                    }
                    catch (InterruptedException e) {

                    }
                }

                sw.suspend();
            }
        });
        this.tStopwatch.start();
    }

    /**
     * resume the stopwatch after paused.
     */
    public void resume() {
        this.start();
    }

    /**
     * pause the stopwatch temporarily.
     */
    public void pause() {
        this.state = STATE.PAUSE;
        try {
            this.tStopwatch.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.tStopwatch = null;
    }

    /**
     * reset the stopwatch
     */
    public void reset() {
        if (this.state == STATE.RUNNING) {
            this.sw.suspend();
        }
        else if (this.state == STATE.STOP) {
            return;
        }

        this.state = STATE.STOP;
        try {
            if (this.tStopwatch != null) {
                this.tStopwatch.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.tStopwatch = null;
        this.sw.stop();
        this.sw.reset();

        this.labelHhmmss.setText(StopwatchController.INIT_HHMMSS);
        this.labelSss.setText(StopwatchController.INIT_SSS);
    }

    /**
     * update the stopwatch GUI display
     */
    private void updateDisplay() {
        long elapsedTime = this.sw.getTime();

        long hr = 0L;
        long min = 0L;
        long sec = 0L;
        long ms = 0L;
        if (elapsedTime < 1000) {
            ms = elapsedTime;
        }
        else if (elapsedTime < 1000 * 60) {
            ms = elapsedTime % 1000;
            sec = elapsedTime / 1000;
        }
        else if (elapsedTime < 1000 * 60 * 60) {
            ms = elapsedTime % 1000;
            sec = (elapsedTime / 1000) % 60;
            min = elapsedTime / (1000 * 60);
        }
        else {
            ms = elapsedTime % 1000;
            sec = (elapsedTime / 1000) % 60;
            min = (elapsedTime / (1000 * 60)) % 60;
            hr = elapsedTime / (1000 * 60 * 60);
        }

        String hhmmss = String.format("%02d:%02d:%02d", hr, min, sec);
        String sss = String.format("%03d", ms);
        logger.debug(String.format("elapsedTime: \"%s\"", elapsedTime));
        logger.debug(String.format("hhmmss: \"%s\"\n", hhmmss));
        logger.debug(String.format("sss: \"%s\"\n", sss));
        this.labelHhmmss.setText(hhmmss);
        this.labelSss.setText(sss);
    }

	public void setLabelHhmmss(Label labelHhmmss) {
		this.labelHhmmss = labelHhmmss;
	}

	public void setLabelSss(Label labelSss) {
		this.labelSss = labelSss;
	}

    /**
     * get stopwatch's current state
     * @return stopwatch's current state
     */
    public STATE getState() {
        return this.state;
    }
}
