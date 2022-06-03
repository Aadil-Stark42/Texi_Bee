package com.mindnotix.texibee.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.mindnotix.texibee.R;
import com.mindnotix.retailers.components.MarkerView;
import com.mindnotix.retailers.data.Entry;
import com.mindnotix.retailers.formatter.IAxisValueFormatter;
import com.mindnotix.retailers.highlight.Highlight;
import com.mindnotix.retailers.utils.MPPointF;

import java.text.DecimalFormat;

@SuppressLint("ViewConstructor")
public class XYMarkerView extends MarkerView {

    private final TextView tvContent;
    private final IAxisValueFormatter xAxisValueFormatter;

    private final DecimalFormat format;

    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
            super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###.0");
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText(String.format("x: %s, y: %s", xAxisValueFormatter.getFormattedValue(e.getX(), null), format.format(e.getY())));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
