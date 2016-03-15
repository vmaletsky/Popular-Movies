package udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 2/28/2016.
 */
public class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.ViewHolder> {
    private List<Movie> mValues;
    private Context mContext;
    private final TypedValue mTypedValue = new TypedValue();

    private String TAG = "PostersAdapter";

    public PostersAdapter(Context context, List<Movie> values) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mContext = context;
        mValues = values;
    }

    public void setValues(List<Movie> values) {
        mValues = values;
    }

    @Override
    public void onBindViewHolder(final PostersAdapter.ViewHolder holder, final int position) {
        String url = "http://image.tmdb.org/t/p/w500/" + mValues.get(position).posterPath;
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                mContext.startActivity(intent);
            }
        });
        Picasso.with(mContext).load(url).into(holder.mImageView);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.poster_image);
        }

    }
}
