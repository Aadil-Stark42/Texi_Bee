package com.mindnotix.retailers.interfaces.dataprovider;

import com.mindnotix.retailers.components.YAxis;
import com.mindnotix.retailers.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
