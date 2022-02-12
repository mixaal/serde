# Serializers Comparison

## Overview

Aim of this project is to compare various serializers under different conditions. The focus is on the following scenarios:

 * small entity, need to (de)serialize into binary/string form
 * lots of small (de)serializations done many times
 * (de)serialization of big object, e.g. snaphot of some resources, etc.

Comparing the following technologies:

 * Gson
 * Jackson
 * Protobuf
 * Custom byte-buffer serializer


## Results

| Technology    | 1 small object | 1000 small objects | 10,000 small objects | 1 object containing 1,000 small objs |
| ------------- | -------------- | ------------------ | -------------------- | ------------------------------------ |
| Jackson       | 36ms/13ms      |  11ms/10ms         |  32ms/35ms           |   8ms/6ms                            |
| Gson          | 4ms/1ms        |  7ms/4ms           |  35ms/24ms           |   4ms/3ms                            |
| Protobuf      | 7ms/2.5ms      |  3.6/2ms           |  12ms/6ms            |   2ms/1ms                            |
| Byte Buffer   | 437μs/43μs     |  4.8/2ms           |  11ms/6ms            |   4ms/2ms                            |

## Conclusion

Jackson seems to have a terrible warm-up time for 1 small entity (20ms), compared to (1-5ms) for Gson. Also Gson improved its performance
when compared to previous versions. The absolute winner in most situations is protobuf. Custom byte-buffer is hard to maintain, but can make sense 
in situations where it's needed to store one small chunk of binary data in cache.
