package com.kekegdsz.img.preview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ky.android.photo.ImagePreviewHelper;

import me.panpf.sketch.SketchImageView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    String[] normalImageUlr = new String[]{
            "https://img1.baidu.com/it/u=1877591048,1703268314&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
            "https://img0.baidu.com/it/u=1350013205,1827373855&fm=253&fmt=auto&app=120&f=JPEG?w=1422&h=800",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201908%2F04%2F20190804185547_oljlj.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1682777828&t=e6644a48edd7aa1dc0bab19dd80bfd08",
            "https://img2.baidu.com/it/u=1998747591,1010535575&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
            "https://img0.baidu.com/it/u=4158527432,2200797161&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889",
            "https://img0.baidu.com/it/u=3715403936,1769851969&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500",
            "https://img2.baidu.com/it/u=5127007,3812041565&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
            "https://img0.baidu.com/it/u=701341020,2312900489&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800",
            "https://img2.baidu.com/it/u=5127007,3812041565&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500",
    };

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent,
                false);
        int size = parent.getContext().getResources().getDisplayMetrics().widthPixels / 3 - 16;
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
        int padding = 16;
        lp.setMargins(padding, padding, padding, padding);
        view.setLayoutParams(lp);
        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.srcImageView.displayImage(normalImageUlr[position]);
        holder.srcImageView.setShowGifFlagEnabled(R.drawable.ic_gif);
        holder.srcImageView.setOnClickListener(srcView -> {
            ImagePreviewHelper
                    .with()
                    .url(normalImageUlr[position])
                    .view(srcView)
                    .start(srcView.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return normalImageUlr.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        SketchImageView srcImageView;

        public MyViewHolder(View view) {
            super(view);
            srcImageView = view.findViewById(R.id.srcImageView);
        }
    }
}