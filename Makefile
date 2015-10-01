JAVAC=javac

OUTDIR=build

sources=$(shell find src -name '*.java')

all: $(OUTDIR)/assembleframes.jar
.PHONY: all

$(OUTDIR)/compile.stamp: $(sources)
	mkdir -p $(OUTDIR)
	$(JAVAC) -d $(OUTDIR) $(sources)
	touch $@

$(OUTDIR)/assembleframes.jar: $(OUTDIR)/compile.stamp
	mkdir -p $(OUTDIR)
	cd $(OUTDIR) && jar cfe $(notdir $@) cdmuhlb.assembleframes.AssembleFrames cdmuhlb

clean:
	rm -rf $(OUTDIR)
