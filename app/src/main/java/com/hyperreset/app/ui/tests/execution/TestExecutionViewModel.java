package com.hyperreset.app.ui.tests.execution;

import android.os.CountDownTimer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Locale;

public class TestExecutionViewModel extends ViewModel {

    private final MutableLiveData<String> timerDisplay = new MutableLiveData<>("00:00");
    private final MutableLiveData<Boolean> timerFinished = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> canProceed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTimerRunning = new MutableLiveData<>(false);
    private CountDownTimer countDownTimer;
    private long timeInMillis = 0;

    public LiveData<String> getTimerDisplay() { return timerDisplay; }
    public LiveData<Boolean> getTimerFinished() { return timerFinished; }
    public LiveData<Boolean> getCanProceed() { return canProceed; }
    public LiveData<Boolean> getIsTimerRunning() { return isTimerRunning; }

    /**
     * Returns the duration in milliseconds for a given test type.
     * Returns 0 for tests without a timer.
     */
    public static long getDurationForTestType(String tipoTest) {
        switch (tipoTest) {
            case "ILLINOIS": return 60_000L;
            case "FLEXION_CODOS": return 30_000L;
            case "ANDERSEN": return 600_000L; // 10 min
            default: return 0L;
        }
    }

    /**
     * Returns true if this test type has a countdown timer.
     */
    public static boolean hasTimer(String tipoTest) {
        return getDurationForTestType(tipoTest) > 0;
    }

    public void setupTimer(long millis) {
        this.timeInMillis = millis;
        timerDisplay.setValue(formatTime(millis));
        timerFinished.setValue(false);
        isTimerRunning.setValue(false);
    }

    /**
     * Starts the countdown timer. Should be called on button click, not on view creation.
     * Disables proceed while timer is running.
     */
    public void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        canProceed.setValue(false);
        isTimerRunning.setValue(true);
        countDownTimer = new CountDownTimer(timeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerDisplay.setValue(formatTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                timerDisplay.setValue("00:00");
                timerFinished.setValue(true);
                isTimerRunning.setValue(false);
                canProceed.setValue(true);
            }
        }.start();
    }

    public void enableProceedAfterDelay() {
        new android.os.Handler(android.os.Looper.getMainLooper())
            .postDelayed(() -> {
                // Only enable if timer is not running
                if (isTimerRunning.getValue() == null || !isTimerRunning.getValue()) {
                    canProceed.setValue(true);
                }
            }, 3000);
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        isTimerRunning.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelTimer();
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
