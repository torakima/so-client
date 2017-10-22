package jp.cosmograph.cosmograph.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import jp.cosmograph.cosmograph.BR;

/**
 * Created by ingyu on 2017-10-17.
 */

public class Status extends BaseObservable {

    private Boolean connect;

    private int feedStatus = 0;

    private Boolean feedComplete = false;

    private Boolean startButton = false;

    @Bindable
    public Boolean getConnect() {
        return connect;
    }

    public void setConnect(Boolean connect) {
        this.connect = connect;
        notifyPropertyChanged(BR.connect);
    }

    @Bindable
    public int getFeedStatus() {
        return feedStatus;
    }

    public void setFeedStatus(int feedStatus) {
        this.feedStatus = feedStatus;
        notifyPropertyChanged(BR.feedStatus);

    }

    @Bindable
    public Boolean getFeedComplete() {
        return feedComplete;
    }

    public void setFeedComplete(Boolean feedComplete) {
        this.feedComplete = feedComplete;
        notifyPropertyChanged(BR.feedComplete);
    }

    @Bindable
    public Boolean getStartButton() {
        return startButton;
    }

    public void setStartButton(Boolean startButton) {
        this.startButton = startButton;
        notifyPropertyChanged(BR.startButton);
    }
}
