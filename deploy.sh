rm -rvf deploy &&
mkdir deploy &&
cp resources/public/index.html deploy &&
clj -m figwheel.main -O advanced -bo prod &&
clj -m t.deploy &&
surge ./deploy bucks2.surge.sh
