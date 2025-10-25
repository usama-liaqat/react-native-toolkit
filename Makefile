all:
ROOT_DIR	:= $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
PARENT_DIR  := $(shell dirname $(ROOT_DIR))
EXAMPLES_DIRS := $(wildcard $(ROOT_DIR)/examples/*)

install: # NPM install
	yarn install

pod-install: # Pod install
	@for dir in $(EXAMPLES_DIRS); do \
		if [ -d $$dir/ios ]; then \
			(cd $$dir && bundle install && bundle exec pod install --project-directory=ios/); \
		fi; \
	done

prepare:
	turbo run prepare
sync: install prepare pod-install

clean: # Clean generated artifacts
	git clean -xdf

resync: clean sync # Full reinstall with deep clean

outdated:
	npm outdated
