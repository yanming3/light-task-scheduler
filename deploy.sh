#!/usr/bin/env bash
LOCAL_PATH=/Users/allan/Works/backend/light-task-scheduler
REMOTE_PATH=/data/applications
VERSION="1.7.0-SNAPSHOT"
pssh -P -h hosts.txt $REMOTE_PATH/lts-admin-$VERSION/bin/admin stop
pssh -P -h hosts.txt $REMOTE_PATH/lts-jobtracker-$VERSION/bin/jobtracker stop


pssh -P -h hosts.txt rm -rf $REMOTE_PATH/lts-admin-$VERSION
pssh -P -h hosts.txt rm -rf $REMOTE_PATH/lts-jobtracker-$VERSION


pscp -h hosts.txt $LOCAL_PATH/lts-admin/target/lts-admin-$VERSION-package.tar.gz $REMOTE_PATH/
pscp -h hosts.txt $LOCAL_PATH/lts-jobtracker/target/lts-jobtracker-$VERSION-package.tar.gz $REMOTE_PATH/


pssh -P -h hosts.txt  tar -xvf $REMOTE_PATH/lts-admin-$VERSION-package.tar.gz -C $REMOTE_PATH/
pssh -P -h hosts.txt  tar -xvf $REMOTE_PATH/lts-jobtracker-$VERSION-package.tar.gz -C $REMOTE_PATH/

pssh -P -h hosts.txt $REMOTE_PATH/lts-admin-$VERSION/bin/admin start
pssh -P -h hosts.txt $REMOTE_PATH/lts-jobtracker-$VERSION/bin/jobtracker start




