# Makefile for recoflow

# Compiler and linker
CXX = g++
CXXFLAGS = -std=c++11 -Wall -O2
LDFLAGS = -lpq  # Link with postgres library

# Include directories
INCLUDES = -I/usr/include/postgresql  # Adjust if your postgresql headers are elsewhere

# Source and object files
SRC_DIR = .  # Assuming source files are in the same directory as the Makefile
SRC_FILES = main.cpp
OBJ_FILES = $(SRC_FILES:.cpp=.o)

# Executable
EXECUTABLE = recoflow

# Build rule for the executable
$(EXECUTABLE): $(OBJ_FILES)
	$(CXX) $(CXXFLAGS) -o $@ $(OBJ_FILES) $(LDFLAGS)

# Build rule for object files (with dependency tracking)
%.o: %.cpp
	$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@
#Build rule with dependency tracking using g++ -MM, which automatically determines header file dependencies.
#%.o: %.cpp
#   $(CXX) $(CXXFLAGS) $(INCLUDES) -MM $< -MF $(@:.o=.d) -c $< -o $@
#   $(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@
#   @$(CXX) -MM $(CXXFLAGS) $(INCLUDES) $< | sed 's,\($*\)\.o:,$(@:.o=.d) \1.o:,g' > $(@:.o=.d)

# Include the dependency files
-include $(OBJ_FILES:.o=.d)


# Default target (same as 'build')
all: $(EXECUTABLE)

# Clean rule
clean:
	rm -f $(OBJ_FILES) $(EXECUTABLE) *.d  # Remove dependency files too

# Phony targets
.PHONY: all cleans