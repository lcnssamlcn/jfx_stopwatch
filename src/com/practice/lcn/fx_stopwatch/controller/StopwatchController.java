package com.practice.lcn.fx_stopwatch.controller;

import javafx.scene.control.Label;
import javafx.application.Platform;

import org.apache.commons.lang.time.StopWatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StopwatchController {
    private static Logger logger = LogManager.getLogger(StopwatchController.class.getName());

    public enum STATE {
        RUNNING,
        PAUSE,
        STOP
    };
    private static final long UPDATE_DISPLAY_INTERVAL = 50;
    private Runnable taskUpdateDisplay = new Runnable() {
        @Override
        public void run() {
            updateDisplay();
        }
    };
    public static final String INIT_HHMMSS = "00:00:00";
    public static final String INIT_SSS = "000";

    private Label labelHhmmss;
    private Label labelSss;

    private volatile STATE state;
    private StopWatch sw;
    private Thread tStopwatch;

    public StopwatchController() {
        this.tStopwatch = null;
        this.state = STATE.STOP;
        this.sw = new StopWatch();
    }

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

    public void resume() {
        this.start();
    }

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

	/**
	* Sets new value of labelHhmmss
	* @param labelHhmmss hhmmss display
	*/
	public void setLabelHhmmss(Label labelHhmmss) {
		this.labelHhmmss = labelHhmmss;
	}

	/**
	* Sets new value of labelSss
	* @param labelSss sss display
	*/
	public void setLabelSss(Label labelSss) {
		this.labelSss = labelSss;
	}

    public STATE getState() {
        return this.state;
    }
}
