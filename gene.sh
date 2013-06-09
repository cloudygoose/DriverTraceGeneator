#!/bin/bash
set -x
ant
sleep 2
java -jar DataBase.jar
sleep 2

cat LegalOutput.txt > pureOutput
cat IllegalOutput.txt >> pureOutput
cp pureOutput output
cat NoiseOutput.txt >> output
mkdir outputDir
cp output outputDir/output
cp pureOutput outputDir/pureOutput
cp badPlates.txt outputDir/badPlates.txt

rm output
rm LegalOutput.txt
rm IllegalOutput.txt
rm pureOutput
rm NoFilterOutput.txt
rm DoublePassFilterOutput.txt
rm readingwayoutput.txt
rm badPlates.txt
rm NoiseOutput.txt
