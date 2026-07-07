package io.agora.agorachatquickstart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NavigationActivity extends AppCompatActivity {
    private static final Integer[] titles = {
            R.string.quick_start,
            R.string.import_messages,
            R.string.send_audio_message,
            R.string.fetch_messages_from_server
    };
    private static final Class<?>[] targetActivities = {
            MainActivity.class,
            ImportMessagesActivity.class,
            SendAudioMessageActivity.class,
            FetchMessagesFromServerActivity.class
    };
    private RecyclerView rvList;
    private Activity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mContext = this;
        rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(new NavigationAdapter());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.item_decoration_drawable));
        rvList.addItemDecoration(itemDecoration);
    }

    private class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationViewHolder> {

        @NonNull
        @Override
        public NavigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_navitation_adapter, parent, false);
            return new NavigationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NavigationViewHolder holder, int position) {
            holder.number.setText((position + 1) + "");
            holder.function.setText(titles[position]);
            final int myPosition = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, targetActivities[myPosition]);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

        private class NavigationViewHolder extends RecyclerView.ViewHolder {
            private TextView number;
            private TextView function;

            public NavigationViewHolder(@NonNull View itemView) {
                super(itemView);
                number = itemView.findViewById(R.id.number);
                function = itemView.findViewById(R.id.function);
            }
        }
    }
}
