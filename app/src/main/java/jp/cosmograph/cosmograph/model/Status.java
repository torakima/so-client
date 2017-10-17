package jp.cosmograph.cosmograph.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import jp.cosmograph.cosmograph.BR;

/**
 * Created by ingyu on 2017-10-17.
 */

public class Status  extends BaseObservable{

    private Boolean connect;

    @Bindable
    public Boolean getConnect() {
        return connect;
    }

    public void setConnect(Boolean connect) {
        this.connect = connect;
        notifyPropertyChanged(BR.connect);
    }
}
