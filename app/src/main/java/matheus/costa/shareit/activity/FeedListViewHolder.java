package matheus.costa.shareit.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import matheus.costa.shareit.R;

/**
 * Created by Matheus on 28/10/2017.
 */

public class FeedListViewHolder extends RecyclerView.ViewHolder {

    private ImageView ivProfileFeed;
    private TextView tvRateFeed;
    private TextView tvMessageFeed;
    private TextView tvNameFeed;
    private TextView tvTimeStampFeed;
    private ImageButton btnLike;

    public FeedListViewHolder(View itemView) {
        super(itemView);

        ivProfileFeed = (ImageView) itemView.findViewById(R.id.ivProfileFeed);
        tvRateFeed = (TextView) itemView.findViewById(R.id.tvRateFeed);
        tvMessageFeed = (TextView) itemView.findViewById(R.id.tvMessageFeed);
        tvNameFeed = (TextView) itemView.findViewById(R.id.tvNameFeed);
        tvTimeStampFeed = (TextView) itemView.findViewById(R.id.tvTimeStampFeed);
        btnLike = (ImageButton) itemView.findViewById(R.id.btnLike);
    }

    public ImageView getIvProfileFeed() {
        return ivProfileFeed;
    }

    public TextView getTvRateFeed() {
        return tvRateFeed;
    }

    public TextView getTvMessageFeed() {
        return tvMessageFeed;
    }

    public TextView getTvNameFeed() {
        return tvNameFeed;
    }

    public TextView getTvTimeStampFeed() {
        return tvTimeStampFeed;
    }

    public ImageButton getBtnLike() {
        return btnLike;
    }
}
