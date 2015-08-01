package com.mmaltuna.mreader.view;

/**
 * Created by miguel on 1/8/15.
 */
public class ProgressBar {
    private android.widget.ProgressBar progressBar;
    private int max = 10000;
    private int inc;
    private int step;
    private int steps;

    public ProgressBar(android.widget.ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void init(int steps) {
        this.steps = steps;
        step = 0;
        inc = max / steps;
        progressBar.setProgress(0);
    }

    public void step() {
        int progress = progressBar.getProgress();
        progressBar.setProgress(progress + inc);
        step++;
    }

    public void show() {
        progressBar.setVisibility(android.widget.ProgressBar.VISIBLE);
    }

    public void hide() {
        progressBar.setVisibility(android.widget.ProgressBar.INVISIBLE);
    }

    public boolean isFinished() { return step == steps; }
}
