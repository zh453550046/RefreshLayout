package uk.co.imallan.jellyrefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    String[] str = new String[]{
            "1sadafsdfsfds",
            "2sadafsdfsfds",
            "3sadafsdfsfds",
            "4sadafsdfsfds",
            "5sadafsdfsfds",
            "6sadafsdfsfds",
            "7sadafsdfsfds",
            "8sadafsdfsfds",
            "9sadafsdfsfds",
            "10sadafsdfsfds",
            "11sadafsdfsfds",
            "12sadafsdfsfds",
            "13sadafsdfsfds",
            "14sadafsdfsfds",
            "15sadafsdfsfds",
            "16sadafsdfsfds",
            "1sadafsdfsfds",
            "2sadafsdfsfds",
            "3sadafsdfsfds",
            "4sadafsdfsfds",
            "5sadafsdfsfds",
            "6sadafsdfsfds",
            "7sadafsdfsfds",
            "8sadafsdfsfds",
            "9sadafsdfsfds",
            "10sadafsdfsfds",
            "11sadafsdfsfds",
            "12sadafsdfsfds",
            "13sadafsdfsfds",
            "14sadafsdfsfds",
            "15sadafsdfsfds",
            "16sadafsdfsfds"
    };
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.addAll(Arrays.asList(str));

        int layout;
        switch (getIntent().getIntExtra(SplashActivity.TYPE, 0)) {
            case 0:
                layout = R.layout.test_main_sc;
                break;
            case 1:
                layout = R.layout.test_main_lv;
                break;
            case 2:
                layout = R.layout.test_main_rc;
                break;
            default:
                layout = R.layout.test_main_sc;
                break;
        }
        setContentView(layout);


        if (layout == R.layout.test_main_rc) {
            recyclerView = (RecyclerView) findViewById(R.id.rc);
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2, GridLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter = new MyAdapter();
            recyclerView.setAdapter(adapter);
        } else if (layout == R.layout.test_main_lv) {
            ListView lv = (ListView) findViewById(R.id.lv);
            lv.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, list));
        }

        final ZRefreshLayout zRefreshLayout = (ZRefreshLayout) findViewById(R.id.ZSpringLayout);
        zRefreshLayout.StartAutoRefresh();
        zRefreshLayout.setOnPullingListenner(new OnPullingListenner() {
            @Override
            public void onPulling(View headView, float percent) {

            }
        });

        zRefreshLayout.setOnReleaseListenner(new OnReleaseListenner() {
            @Override
            public void onRelease(View headView) {
                RotateLoading rotateLoading = (RotateLoading) headView.findViewById(R.id.loading);
                headView.findViewById(R.id.ll).animate().alpha(1).setDuration(200);
                rotateLoading.start();
            }

            @Override
            public void onFinishRefresh(View headView) {
                RotateLoading rotateLoading = (RotateLoading) headView.findViewById(R.id.loading);
                headView.findViewById(R.id.ll).setAlpha(0);
                rotateLoading.stop();
            }
        });

        zRefreshLayout.setOnRefreshListener(new ZRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                zRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        zRefreshLayout.finishRefresh();
                    }
                }, 3000);
            }
        });

    }

    class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            holder.tv.setText(list.get(position));

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    class MyHolder extends RecyclerView.ViewHolder {
        public MyHolder(View itemView) {
            super(itemView);
            tv = ((TextView) (itemView.findViewById(R.id.tv)));
        }

        public TextView tv;
    }

}
