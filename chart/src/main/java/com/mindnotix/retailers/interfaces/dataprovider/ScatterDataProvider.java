package com.mindnotix.retailers.interfaces.dataprovider;

import com.mindnotix.retailers.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
