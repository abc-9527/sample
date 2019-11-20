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
            , "https://cdn.duitang.com/uploads/item/201412/12/20141212125544_VxziA.thumb.700_0.jpeg"
            , "http://cdn.duitang.com/uploads/item/201411/15/20141115192137_jRBYY.thumb.700_0.png"
            , "http://pic1.zhimg.com/50/v2-7b7eae4072d32ea55a79b0b9f1dbcf4e_hd.jpg"
            , "http://pic4.zhimg.com/50/v2-fe99f3e860f6a0f0eefbd2fd38ac0624_hd.jpg"
            , "http://cdn.duitang.com/uploads/item/201411/15/20141115192137_jRBYY.thumb.700_0.png"
            , "http://pic1.zhimg.com/50/v2-7b7eae4072d32ea55a79b0b9f1dbcf4e_hd.jpg"
            , "http://pic4.zhimg.com/50/v2-fe99f3e860f6a0f0eefbd2fd38ac0624_hd.jpg"
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
