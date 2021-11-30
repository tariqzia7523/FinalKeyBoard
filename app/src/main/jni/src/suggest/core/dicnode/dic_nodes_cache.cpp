#include <list>

#include "defines.h"
#include "suggest/core/dicnode/dic_node_priority_queue.h"
#include "suggest/core/dicnode/dic_node_utils.h"
#include "suggest/core/dicnode/dic_nodes_cache.h"

namespace latinime {

// The biggest value among MAX_CACHE_DIC_NODE_SIZE, MAX_CACHE_DIC_NODE_SIZE_FOR_SINGLE_POINT, ...
const int DicNodesCache::LARGE_PRIORITY_QUEUE_CAPACITY = 310;
// Capacity for reducing memory footprint.
const int DicNodesCache::SMALL_PRIORITY_QUEUE_CAPACITY = 100;

}  // namespace latinime
