#ifndef L2ASSOCIATIVECACHE_H
#define L2ASSOCIATIVECACHE_H

#define DEBUG 0
#define L2_ASSOCIATIVITY 2
#define L1_NUM_BLOCKS (L1_SIZE / BLOCK_SIZE)
#define L2_NUM_SETS (L2_SIZE / (BLOCK_SIZE * L2_ASSOCIATIVITY))

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../Cache.h"

void resetTime();

uint32_t getTime();

/****************  RAM memory (byte addressable) ***************/
void accessDRAM(uint32_t, uint8_t *, uint32_t);

/*********************** Cache *************************/

void initCache();
void destroyCache();
void accessL1(uint32_t, uint8_t *, uint32_t);
void accessL2(uint32_t, uint8_t *, uint32_t);
int indexL1(uint32_t address);
int indexL2(uint32_t address);

typedef struct CacheLine {
    uint8_t valid;
    uint8_t dirty;
    uint32_t tag;
    uint8_t block[BLOCK_SIZE];
} CacheLine;

typedef struct AssociativeCacheLine {
    uint8_t valid;
    uint8_t dirty;
    uint32_t tag;
    uint32_t time;
    uint8_t block[BLOCK_SIZE];
} AssociativeCacheLine;

typedef struct Cache {
    uint32_t init;
    CacheLine* lines;
} Cache;

typedef struct AssociativeCache {
    uint32_t init;
    AssociativeCacheLine lines[L2_NUM_SETS][L2_ASSOCIATIVITY];
} CacheL2Associative;

/*********************** Interfaces *************************/

void read(uint32_t, uint8_t *);

void write(uint32_t, uint8_t *);

/* Debugging */

/* prints L1 contents (only non-empty addresses) */
void printL1();
void printL2();
void printDRAM();
void printAll();
void debug(char *);

#endif
