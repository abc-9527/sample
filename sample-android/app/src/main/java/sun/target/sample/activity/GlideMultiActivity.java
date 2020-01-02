package sun.target.sample.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Arrays;

import sun.target.R;
import sun.target.img.glide.MultiBitmapUrl;
import sun.target.sample.widget.DisplayUtils;

public class GlideMultiActivity extends AppCompatActivity {

    private final String[] urls = new String[]{
            "https://img3.duitang.com/uploads/item/201605/02/20160502110219_NKXRw.thumb.700_0.jpeg"
            , "http://img3.duitang.com/uploads/item/201408/27/20140827155741_iB3SF.thumb.700_0.jpeg"
            , "http://b-ssl.duitang.com/uploads/item/201703/26/20170326161532_aGteC.jpeg"
            , "http://cdn.duitang.com/uploads/item/201411/15/20141115192137_jRBYY.thumb.700_0.png"
            , "http://5b0988e595225.cdn.sohucs.com/images/20171114/0fc43e9ad58f4a5cb41a018925b0e475.jpeg"
            , "http://pic4.zhimg.com/50/v2-e08b1459268338075ffecff0d6ec2d08_hd.jpg"
            , "http://cdn.duitang.com/uploads/item/201411/15/20141115192137_jRBYY.thumb.700_0.png"
            , "http://b-ssl.duitang.com/uploads/item/201804/18/20180418185826_skjbx.jpg"
            , "http://image.biaobaiju.com/uploads/20180803/23/1533308847-sJINRfclxg.jpeg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_multi);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        final int size = DisplayUtils.dip2px(96, getResources().getDisplayMetrics());

        final int space = DisplayUtils.dip2px(12, getResources().getDisplayMetrics());
        final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(size, size);
        recyclerView.addItemDecoration(new ItemDecoration(space));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(lp);
                return new RecyclerView.ViewHolder(imageView) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                View view = holder.itemView;
                if (view instanceof ImageView) {
                    if (position < 8) {
                        Glide.with(GlideMultiActivity.this)
                                .load(new MultiBitmapUrl(getDrawable(R.mipmap.ic_launcher)
                                        , Arrays.copyOfRange(urls, 0, position + 1)))
                                .into((ImageView) view);
                    } else {
                        Glide.with(GlideMultiActivity.this)
                                .load(new MultiBitmapUrl(getDrawable(R.mipmap.ic_launcher)
                                        , urls))
                                .into((ImageView) view);
                    }
                }
            }

            @Override
            public int getItemCount() {
                return urls.length;
            }
        });
    }

    private final static class ItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        ItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.top = space;
        }
    }
}
