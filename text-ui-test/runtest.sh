#!/usr/bin/env bash

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]
then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# compile the code into the bin folder, terminates if error occurred
if ! javac -cp ../src/main/java -Xlint:none -d ../bin ../src/main/java/*.java
then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ../bin AlphaOne < input.txt > ACTUAL.TXT

# convert to UNIX format
# normalize CRLF -> LF using tr (portable on macOS)
tr -d '\r' < ACTUAL.TXT > ACTUAL-UNIX.TXT
tr -d '\r' < EXPECTED.TXT > EXPECTED-UNIX.TXT

# remove lines that exactly match any input commands (so echoed inputs don't cause mismatches)
if [ -s "input.txt" ]; then
    grep -F -x -v -f "input.txt" "ACTUAL-UNIX.TXT" > "ACTUAL-NOINPUT.TXT" || true
    grep -F -x -v -f "input.txt" "EXPECTED-UNIX.TXT" > "EXPECTED-NOINPUT.TXT" || true
else
    cp "ACTUAL-UNIX.TXT" "ACTUAL-NOINPUT.TXT"
    cp "EXPECTED-UNIX.TXT" "EXPECTED-NOINPUT.TXT"
fi

# remove trailing whitespace from each line to avoid failures caused by insignificant spacing
# use sed -E (portable on macOS) to trim trailing spaces/tabs
sed -E 's/[[:space:]]+$//' "ACTUAL-NOINPUT.TXT" > "ACTUAL-CLEAN.TXT"
sed -E 's/[[:space:]]+$//' "EXPECTED-NOINPUT.TXT" > "EXPECTED-CLEAN.TXT"

# trim trailing blank lines and ensure exactly one trailing newline on each cleaned file
for f in "ACTUAL-CLEAN.TXT" "EXPECTED-CLEAN.TXT"; do
    awk '{lines[NR]=$0} END{n=NR; while(n>0 && lines[n]=="") n--; for(i=1;i<=n;i++) print lines[i]} ' "$f" > "$f".tmp
    printf '\n' >> "$f".tmp
    mv "$f".tmp "$f"
done

# compare the cleaned outputs (unified diff for easier debugging)
if diff -u "EXPECTED-CLEAN.TXT" "ACTUAL-CLEAN.TXT" > "DIFF.TXT"
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    echo "--- Begin diff ---"
    cat "DIFF.TXT"
    echo "---- End diff ----"
    exit 1
fi