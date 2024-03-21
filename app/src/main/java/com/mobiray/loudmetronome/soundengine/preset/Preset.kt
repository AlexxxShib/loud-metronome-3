package com.mobiray.loudmetronome.soundengine.preset


data class Preset(
    var isPlaying: Boolean = false,
    var name: String? = null,
    val segmentList: MutableList<Segment> = mutableListOf()
) {

    constructor(firstSegment: Segment) : this() {
        segmentList.add(firstSegment)
    }

    fun setSegmentList(segmentList: List<Segment>) {
        this.segmentList.clear()
        this.segmentList.addAll(segmentList)
    }

    fun getSegment(segmentIndex: Int = 0): Segment {
        return segmentList[segmentIndex]
    }
}
