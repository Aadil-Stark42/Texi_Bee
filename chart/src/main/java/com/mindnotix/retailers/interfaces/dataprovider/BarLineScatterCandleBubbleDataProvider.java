package com.mindnotix.retailers.interfaces.dataprovider;

import com.mindnotix.retailers.components.YAxis.AxisDependency;
import com.mindnotix.retailers.data.BarLineScatterCandleBubbleData;
import com.mindnotix.retailers.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
