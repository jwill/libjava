# Makefile template for Java.
# Please use the newer ANT build instead.
# This does not build the tests files and may not be always up to date.
# This also might not work on Windows.
#
# The following vars are available for customizing the build process:
#
# LIB_VER [Default=none] :
#   Version of the library. If this is blank, the output jar file names
#   will not contain the version.
#   For e.g., LIB_VER = 1.0.0
#
# LIB_NAME [Required] :
#   Name of the java lib for naming the output jar files.
#   For e.g., LIB_NAME = libjava
#
# LIBS [Default=none] :
#   Relative paths (from Makefile) to external jar files.
#   For e.g., LIBS = logback-1.0/logback.jar libxml/libxml.jar
#
# MAIN_CLASS [Default=none] :
#   Name of main class along with full package path.
#   For e.g., MAIN_CLASS = com.pkg.App
#
# TARGET_VERSION [Default=1.5] :
#   Target JVM version to compile for. 
#   For e.g., TARGET_VERSION = 1.5
#
# Output vars (created by this template):
# SRCS :
#   Space-separated paths to each individual .java file 
#   For e.g., SRCS = src/main/com/pkg/Util.java 
#                    src/main/com/pkg/InputStream.java
#
# CLASSES :
#   Space-separated paths to each individual compiled .class file 
#   For e.g., CLASSES = bin/main/com/pkg/Util.class 
#                       bin/main/com/pkg/InputStream.class
#
# JAVAC_OPTS : all command-line options to javac
# JAVADOC_OPTS : all command-line options to javadoc
#

PATH_SEP = :
DIR_SEP = / # unused for now since Windows can handle / ?

ifeq ($(strip $(TARGET_VERSION)),)
    TARGET_VERSION = 1.5
endif

ifeq ($(strip $(LIB_VER)),)
    LIB_VER_SUFFIX = 
else
    LIB_VER_SUFFIX = -$(strip $(LIB_VER))
endif

LIB_NAME_VER = $(strip $(LIB_NAME))$(LIB_VER_SUFFIX)

SRC_DIR = src
SRC_MAIN_DIR = $(SRC_DIR)/main

BIN_DIR = bin
BIN_MAIN_DIR = $(BIN_DIR)/main
BIN_DEP_DIR = $(BIN_DIR)/dep

DIST_DIR = dist$(LIB_VER_SUFFIX)
DOC_DIR = $(DIST_DIR)/doc

DEST_MAIN_JAR_FILE = $(LIB_NAME_VER).jar
DEST_DEP_JAR_FILE = $(LIB_NAME_VER)-dep.jar
DEST_DIST_FILE = $(LIB_NAME_VER).zip

# TMP1_ = src/j/io/*.java etc....
#TMP1_ = $(addsuffix /*.java,$(addprefix $(SRC_DIR)/,$(PKGS)))

# Expand the wildcards to get the actual file names
#SRCS = $(foreach dir,$(TMP1_),$(wildcard $(dir)))

SRCS = $(shell find $(SRC_MAIN_DIR) -iname *.java)

TMP2_ = $(SRCS:.java=.class)
CLASSES = $(TMP2_:$(SRC_MAIN_DIR)/%=$(BIN_MAIN_DIR)/%)

TMP3_ = $(addsuffix $(PATH_SEP),$(SRC_MAIN_DIR)/ $(LIBS))
CLASSPATHS = $(subst $(PATH_SEP) ,$(PATH_SEP),$(TMP3_))
LIBS_OPTS = -cp "$(CLASSPATHS)"

LIBS_TARGETS = $(addprefix $(BIN_DEP_DIR)/,$(LIBS:.jar=.xxx))

ifeq ($(strip $(MAIN_CLASS)),)
    JAR_OPTS = cf
else
    JAR_OPTS = cfe
endif

JAVAC_OPTS = $(LIBS_OPTS) -g:lines,vars,source -Xlint:unchecked \
    -source "$(TARGET_VERSION)" \
    -target "$(TARGET_VERSION)" -d "$(BIN_MAIN_DIR)"

TMP4_ = $(sort $(dir $(SRCS)))
TMP5_ = $(TMP4_:%/=%)
PKGS = $(TMP5_:$(SRC_MAIN_DIR)/%=%)
PKGS_PROPER = $(subst /,.,$(PKGS))

JAVADOC_OPTS = -source "$(TARGET_VERSION)" -sourcepath "$(SRC_MAIN_DIR)/" \
    -protected -docencoding UTF-8 -charset UTF-8 -classpath "$(CLASSPATHS)" \
    -windowtitle "$(LIB_NAME) $(LIB_VER)" \
    -keywords -d "$(DOC_DIR)" $(PKGS_PROPER)

###
### targets
###
.PHONY: all init docs dist clean

all: init $(DIST_DIR)

init:
	@mkdir -p "$(BIN_MAIN_DIR)"
	@mkdir -p "$(BIN_DEP_DIR)"
	@echo "Compiling using: javac $(JAVAC_OPTS)"

docs: init $(DOC_DIR)

dist: init $(DEST_DIST_FILE)

clean:
	rm -rf "$(DEST_DIST_FILE)" "$(BIN_DIR)" "$(DIST_DIR)" *~

###
### rules
###

### This indicates do not remove intermediate files
.SECONDARY:

### This expands the external jar files into bin for later combination
$(BIN_DEP_DIR)/%.xxx: %.jar  
	@mkdir -p "$(dir $(BIN_DEP_DIR)/$*.xxx)"
	cd "$(BIN_DEP_DIR)"; jar -xf "../$<"
	@echo -n > "$@"

$(BIN_DIR)/%.class: $(SRC_DIR)/%.java $(LIBS_TARGETS)
	@echo "Compiling \"$<\" ..."
	@javac $(JAVAC_OPTS) $< 

$(DIST_DIR)/$(DEST_MAIN_JAR_FILE): $(CLASSES) 
	@mkdir -p "$(DIST_DIR)"
	@echo "Warning: any *.xml files are not copied to $(BIN_MAIN_DIR)"
	jar $(JAR_OPTS) "$@" $(MAIN_CLASS) -C "$(BIN_MAIN_DIR)" .

$(DIST_DIR)/$(DEST_DEP_JAR_FILE): $(LIBS_TARGETS)
	@mkdir -p "$(DIST_DIR)"
	jar cf "$@" -C "$(BIN_DEP_DIR)" .

$(DIST_DIR): $(DIST_DIR)/$(DEST_MAIN_JAR_FILE) \
             $(DIST_DIR)/$(DEST_DEP_JAR_FILE)
	@touch "$(DIST_DIR)"

$(DOC_DIR): $(DIST_DIR)
	@mkdir -p "$(DOC_DIR)"
	javadoc $(JAVADOC_OPTS)
	@touch "$(DOC_DIR)"

$(DEST_DIST_FILE): $(DIST_DIR) $(DOC_DIR)
	zip -r -9 -q "$(DEST_DIST_FILE)" "$(DIST_DIR)"

