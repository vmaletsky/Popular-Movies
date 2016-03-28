package udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 2/28/2016.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Movie> mValues;
    private Context mContext;
    private final TypedValue mTypedValue = new TypedValue();

    private String TAG = getClass().getSimpleName();

    public boolean add(Movie m) {
        return mValues.add(m);
    }

    public boolean addAll(List<Movie> movies) {
        return mValues.addAll(movies);
    }

    public MoviesAdapter(Context context) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mContext = context;
        mValues = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.ViewHolder holder, final int position) {
        String url = mContext.getString(R.string.posters_base_url) + mValues.get(position).posterPath;
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra("MOVIE", mValues.get(position));
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
