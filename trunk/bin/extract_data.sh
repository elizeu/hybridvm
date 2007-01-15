#!/bin/bash

grep -w AverageShare result.10-b.res | cut -d: -f2 > share.res
grep -w LeasingAverageShare result.10-b.res | cut -d: -f2 > leasingshare.res
grep -w AuctionAverageShare result.10-b.res | cut -d: -f2 > auctionshare.res

grep "events in the queue" result.10-b.res | awk '{ print $6 }' > requests.res

grep -w AveragePrice result.10-b.res | cut -d: -f2 > price.res
grep -w LeasingAveragePrice result.10-b.res | cut -d: -f2 > leasingprice.res
grep -w AuctionAveragePrice result.10-b.res | cut -d: -f2 > auctionprice.res