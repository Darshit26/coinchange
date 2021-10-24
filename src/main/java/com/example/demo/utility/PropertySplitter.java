package com.example.demo.utility;

import com.google.common.base.Splitter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("PropertySplitter")
public class PropertySplitter {
    public Map<String, String> map(String property){
        return Splitter.on(',').omitEmptyStrings().trimResults().withKeyValueSeparator(":").split(property);
    }
}
