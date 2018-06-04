package com.zhy.flowlayout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.w3c.dom.Text;

/**
 * Created by zhy on 15/9/10.
 */
public class SimpleFragment extends Fragment {
    private String[] mVals = new String[]
            {"Hello", "Android", "Weclome Hi ", "Button", "TextView", "Hello",
                    "Android", "Weclome", "Button ImageView", "TextView", "Helloworld",
                    "Android", "Weclome Hello", "Button Text", "TextView"};// 15

    private TagFlowLayout mFlowLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final LayoutInflater mInflater = LayoutInflater.from(getActivity());
        mFlowLayout = (TagFlowLayout) view.findViewById(R.id.id_flowlayout);
        mFlowLayout.setAdapter(new TagAdapter<String>(mVals) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        view.findViewById(R.id.switchBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMore((TextView)v);
                Log.i("FlowLayout", "width:"+v.getWidth()+", height:"+v.getHeight());
                mFlowLayout.switchShow();
            }
        });
    }

    boolean isMore = true;

    private void updateMore(TextView titleTv) {
        isMore = !isMore;
        if (isMore) {
            Drawable drawable =getResources().getDrawable(R.mipmap.icon_arrow_right);
            drawable.setBounds(0, 0,drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            titleTv.setCompoundDrawables(null, null,drawable, null);//画在右边
            titleTv.setText("更多");
        } else {
            Drawable drawable =getResources().getDrawable(R.mipmap.icon_arrow_down);
            drawable.setBounds(0, 0,drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            titleTv.setCompoundDrawables(null, null,drawable, null);//画在右边
            titleTv.setText("收起");
        }

    }
}
