# pictura

pictura is a command line tool to process a list of image URLs and output the top three colors found in them to a CSV 
file.

## Usage

```
Usage: pictura [-hV] <inputFilePath> <outputFilePath>
Finds the top pixel color values in a list of images.
      <inputFilePath>    A path to a file containing a list of image URLs.
      <outputFilePath>   A path to the output file to write the color data.
  -h, --help             Show this help message and exit.
  -V, --version          Print version information and exit.
```

## Design

### Queue pipelining

pictura uses a simple queue pipelining architecture to eliminate processing bottlenecks. Since pictura operates on one 
input file, pipelines do not run forever and use poison pill markers in the queue to determine when to shut down.

Each pipeline has a discrete job:

1. `ReadInputFilePipeline`: Reads from the input file and puts URLs in the `imageUrlQueue`.
2. `DownloadImagePipeline`: Reads from the `imageUrlQueue` and puts paths to images to the `imageFileQueue`.
3. `FindTopColorsPipeline`: Reads from the `imageFileQueue` and puts color results in the `imageResultQueue`.
4. `WriteOutputFilePipeline`: Reads from the `imageResultQueue` and appends results to the output file.

### Large files

Since pictura buffers all reads and writes, it should never run into memory issues with large input or output files.

### Logging

pictura uses a logging decorator to log important pipeline activity.

## Possible improvements

### Adding thread scaling

pictura supports scaling pipelines to multiple threads. Arguments could be added to determine the number of threads 
and/or an automatically calculated number of threads could be spun up to maximize CPU usage.

In a more robust system these pipelines could be moved to different deployables and operate on shared queues.

### Adding tests

pictura would benefit greatly from unit and integration tests, but currently has none as it was developed as a spike 
focusing on resource utilization. Ideally a system like this would be developed with tests from the start to force code
to be testable, to enable a refactoring safety net, to help with CI/CD, etc.

### Refactoring PipelineRunner

Currently the poison pill queue concept is coupled with the pipeline runner concept. There is an opportunity to refactor 
these into separate concerns that would be beneficial for adding different types of pipelines.

### Refactoring data models

Pipelines use the same data model as queue items, even though some data is redundant/unnecessary at certain steps.

### Ordering output

Currently output to the CSV file is not guaranteed to be in input file order. If desired, guaranteed output order could 
be accomplished in a couple of ways; for example, results could be collected by querying an external database with 
knowledge of the ordering or by simply combining partial result files that are guaranteed to be ordered.

### Speeding up image processing

The image processing speed could be improved, as there are faster ways to scan through pixels in an image.
