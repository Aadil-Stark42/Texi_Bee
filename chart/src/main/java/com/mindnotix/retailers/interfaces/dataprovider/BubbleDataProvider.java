package com.mindnotix.retailers.interfaces.dataprovider;

import com.mindnotix.retailers.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
