package com.kekegdsz.img.preview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ky.android.photo.ImagePreviewHelper;
import com.ky.android.photo.bean.ImageModel;
import com.ky.android.photo.config.ImageType;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.SketchImageView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private List<ImageModel> models = new ArrayList<>();

    public GridAdapter() {
        String[] normalImageUlrs = new String[]{
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
        for (String url : normalImageUlrs) {
            ImageModel imageModel = new ImageModel(url, url, ImageType.IMAGE);
            models.add(imageModel);
        }

        String[] videos = new String[]{
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=v0200f470000bnj24h87q8i137v8k2t0&line=0&ratio=540p&watermark=1&media_type=4&vr_type=0&improve_bitrate=0&logo_name=aweme",
                "https://aweme.snssdk.com/aweme/v1/play/?video_id=v0200f120000bnj4u9egncodds6bjgm0&line=0&ratio=540p&watermark=1&media_type=4&vr_type=0&improve_bitrate=0&logo_name=aweme",

        };

        String[] videoImgs = new String[]{
                "http://p1-dy.byteimg.com/large/tos-cn-p-0015/d6addaee76f3495d840d6dff8d2216e0_1575363173.jpeg?from=2563711402_large",
                "http://p3-dy.byteimg.com/large/tos-cn-p-0015/818d1fad6be3458e940dcd4b5e3bdaf9_1575374652.jpeg?from=2563711402_large"
        };

        for (int i = 0; i < videos.length; i++) {
            ImageModel videoModel = new ImageModel(videos[i], videoImgs[i], ImageType.VIDEO);
            models.add(videoModel);
        }
    }

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
        holder.srcImageView.displayImage(models.get(position).getThumb());
        holder.srcImageView.setShowGifFlagEnabled(R.drawable.ic_gif);
        holder.srcImageView.setOnClickListener(srcView -> {
            ImagePreviewHelper
                    .with()
                    .setModels(models)
                    .position(position)
                    .view(srcView)
                    .start(srcView.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        SketchImageView srcImageView;

        public MyViewHolder(View view) {
            super(view);
            srcImageView = view.findViewById(R.id.srcImageView);
        }
    }
}