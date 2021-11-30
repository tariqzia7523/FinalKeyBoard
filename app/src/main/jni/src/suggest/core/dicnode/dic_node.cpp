#include "suggest/core/dicnode/dic_node.h"

namespace latinime {

DicNode::DicNode(const DicNode &dicNode)
        :
#if DEBUG_DICT
          mProfiler(dicNode.mProfiler),
#endif
          mDicNodeProperties(dicNode.mDicNodeProperties), mDicNodeState(dicNode.mDicNodeState),
          mIsCachedForNextSuggestion(dicNode.mIsCachedForNextSuggestion) {
    /* empty */
}

DicNode &DicNode::operator=(const DicNode &dicNode) {
#if DEBUG_DICT
    mProfiler = dicNode.mProfiler;
#endif
    mDicNodeProperties = dicNode.mDicNodeProperties;
    mDicNodeState = dicNode.mDicNodeState;
    mIsCachedForNextSuggestion = dicNode.mIsCachedForNextSuggestion;
    return *this;
}

} // namespace latinime
