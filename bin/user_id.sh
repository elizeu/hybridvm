#!/bin/bash

grep -v \; SDSC-DS-2004-1.swf | awk '{ print $12 }' | sort -n | uniq -c
