package sun.target;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sun.target.sample.activity.GlideMultiActivity;
import sun.target.sample.activity.PathRunActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Adapter adapter = new Adapter() {
            @Override
            void OnItemClick(Item it) {
                Intent intent = new Intent(MainActivity.this, it.activity);
                startActivity(intent);
            }
        };
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.add(new Item("PATH running", PathRunActivity.class));
        adapter.add(new Item("群头像(Glide)", GlideMultiActivity.class));
    }


    static final class Item {
        private String name;
        private Class<? extends AppCompatActivity> activity;

        Item(String name, Class<? extends AppCompatActivity> activity) {
            this.name = name;
            this.activity = activity;
        }
    }

    static abstract class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<Item> mDatas = new ArrayList<>();

        public final void add(Item it) {
            mDatas.add(it);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final RecyclerView.ViewHolder holder = new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false)) {
            };
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClick(mDatas.get(holder.getAdapterPosition()));
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.itemView instanceof TextView) {
                ((TextView) holder.itemView).setText(mDatas.get(position).name);
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        abstract void OnItemClick(Item it);
    }
}
