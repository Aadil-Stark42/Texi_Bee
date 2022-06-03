package com.mindnotix.retailers.interfaces.dataprovider;

import com.mindnotix.retailers.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
