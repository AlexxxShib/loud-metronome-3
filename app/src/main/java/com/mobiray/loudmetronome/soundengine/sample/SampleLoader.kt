package com.mobiray.loudmetronome.soundengine.sample

import android.content.Context
import com.mobiray.loudmetronome.R
import java.io.DataInputStream

object SampleLoader {

    private const val ASSETS_PATH = ""

    private val suffixList = arrayOf("low", "mid", "hi")

    fun load(context: Context, path: String): ByteArray {
        try {
            val fileDescriptor = context.assets.openFd(path)
            val size = fileDescriptor.getLength().toInt()

            val sample = ByteArray(size)
            val dataInputStream = DataInputStream(context.assets.open(path))

            dataInputStream.readFully(sample)

            return sample;
        } catch (e: Throwable) {
            e.printStackTrace()

            throw IllegalStateException("Failed to load sample: $path")
        }
    }

    fun getSampleList(context: Context): List<Sample> {

        val samples = mutableListOf<Sample>()
        val sampleFilesArray = context.resources.getStringArray(R.array.samples_prefixes)
        val sampleLabelsArray = context.resources.getStringArray(R.array.samples_labels)

        var i = 0
        while (i < sampleFilesArray.size && i < sampleLabelsArray.size) {
            val filename = sampleFilesArray[i]
            val label = sampleLabelsArray[i]
            val sample = createSample(context, filename, label)

            samples.add(sample)
            i++
        }

        return samples
    }

    private fun createSample(context: Context, filename: String, label: String): Sample {
        val assetFilesList = context.assets.list(ASSETS_PATH)?.toList()
            ?: throw IllegalStateException("Error during getting assets!")

        val samplePaths = mutableListOf<String>()
        for (suffix in suffixList) {
            val path = String.format("%s-%s.%s", filename, suffix, "wav")

            if (assetFilesList.contains(path)) {
                samplePaths.add(ASSETS_PATH + path)
            } else {
                throw IllegalStateException("File $filename is not found!")
            }
        }

        return Sample(
            filename = filename,
            label = label,
            samplePaths = samplePaths
        )
    }
}

