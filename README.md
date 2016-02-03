PhyTreeSearch
=============

The goal of this project is to develop a tool that is able to 
extract "interesting" subtrees of
large phylogenetic trees, that contain sequences of proteins (or protein fragments)
at the leaves. 

The user can specify (by editing a simple properties file)
what counts to be "interesting" in terms of:
 - an amino-acid pattern occuring in leaf sequences
 - a minimum threshold 'P' so that only subtrees that contain at least 'P'
percent of leaves matching the pattern are returned
 - a minimum tree size in terms of leaf count or tree height

The program works by reading in a properties file that defines the inputs,
outputs, and other parameters. A sample configuration file:

    treeFilesDir = /home/.../level_4/
    fastaFilesDir = /home/.../clusters_fasta/
    outputTreeFilesDir = /home/.../level_5/weka_c50_min7_55/
    seqPattern = HD
    minLeafNum = 7
    minPatternPercent = 55
    treeColors = yes

### How to compile

You'll need java and [gradle](http://www.gradle.org/downloads "Gradle") (1.6 or newer).

Then you can build with:

    gradle build

### How to use

Examine and run the runExample.sh script.


