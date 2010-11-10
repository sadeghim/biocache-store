#!/bin/sh
echo 'Running occurrences.sql against portal...'
mysql -uroot -ppassword portal < /data/bie-staging/biocache/db/occurrences.sql
date
echo removing empty files
find /data/bie-staging/biocache/tmp -type f -size 0 -exec rm -f {} \;
date
echo Concat files
cat /data/bie-staging/biocache/tmp/header.txt /data/bie-staging/biocache/tmp/file.out.* > /data/bie-staging/biocache/occurrences.csv
date

# echo "Fixing escaping of quotes..."  THIS IS NOW DONE BY THE ORIGINAL DUMP
# date
# sed -e 's/\\"/""/g' /data/bie-staging/biocache/occurrences-tmp.csv > /data/bie-staging/biocache/occurrences.tmp2.csv
# sed -e 's/\\""/\\"/g' /data/bie-staging/biocache/occurrences.tmp2.csv > /data/bie-staging/biocache/occurrences.csv
date
echo "Zipping occurrences..."
gzip occurrences.csv

date
echo "Script has finished."
