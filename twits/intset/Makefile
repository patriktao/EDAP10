CFILES  = example.c intset.c unittest.c
HFILES  = intset.h
OFILES  = $(CFILES:.c=.o)
PROGS   = example unittest

# -----------------------------------------------------------------------------

CFLAGS  = -std=c99 -O2 -g -Wall -Wextra -Werror -Wfatal-errors
LDFLAGS =
CC      = gcc
LD      = gcc

# explicit -pthread option not needed on MacOS (a.k.a. "Darwin")
# (and results in warnings on some older MacOS versions)
ifneq ($(shell uname),Darwin)
  LDFLAGS += -pthread
endif

# -----------------------------------------------------------------------------

.PHONY: all clean
.INTERMEDIATE: $(OFILES)

all: $(PROGS)

clean:
	$(RM) $(OFILES) $(PROGS)

$(OFILES): $(HFILES)

$(PROGS): $(OFILES)
	$(LD) intset.o $(LDFLAGS) $@.o -o $@
