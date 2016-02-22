package uk.co.imallan.jellyrefreshlayout;

import android.view.View;

/**
 * Created by Administrator on 2016/2/4.
 */
public interface OnReleaseListenner {
    void onRelease(View headView);

    void onFinishRefresh(View headView);
}
