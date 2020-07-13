# SAMDescribe
Tools for analyzing metadata for SAM/BAM/CRAM alignment files.

Currently only for CRAM, BAI and CRAI.

Build with gradle:
`./gradlew shadowJar`

Supply a CRAM file as an argument:
`java -jar ./build/libs/SAMDescribe*.jar --target-path <cramfile>`
