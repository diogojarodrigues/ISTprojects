#include "L2AssociativeCache.h"

uint8_t DRAM[DRAM_SIZE];
uint32_t time;
Cache L1;
CacheL2Associative L2;

/**************** Time Manipulation ***************/
void resetTime() { time = 0; }

uint32_t getTime() { return time; }

/****************  RAM memory (byte addressable) ***************/
void accessDRAM(uint32_t address, uint8_t *data, uint32_t mode) {
    // /* debug */
    // char temp_str[64];
    // sprintf(temp_str, "DRAM: 0x%06X | Mode: %s", address,
    //         mode ? "READ" : "WRITE");
    // debug(temp_str);

    // Adress out of range
    if (address >= DRAM_SIZE - WORD_SIZE + 1) exit(-1);

    if (mode == MODE_READ) {
        memcpy(data, &(DRAM[address]), BLOCK_SIZE);
        time += DRAM_READ_TIME;
    }

    if (mode == MODE_WRITE) {
        memcpy(&(DRAM[address]), data, BLOCK_SIZE);
        time += DRAM_WRITE_TIME;
    }
}

/*********************** Cache *************************/

void initCache() {
    L1.lines = (CacheLine *)malloc(L1_SIZE / BLOCK_SIZE * sizeof(CacheLine));

    /* init all values to 0 */
    for (int i = 0; i < L1_NUM_BLOCKS; i++) {
        L1.lines[i].dirty = 0;                  // Dirty bit
        L1.lines[i].valid = 0;                  // Valid bit
        L1.lines[i].tag = 0;                    // Address tag
        for (int k = 0; k < BLOCK_SIZE; k++) {  // Fill data with 0s
            L1.lines[i].block[k] = 0;
        }
    }
    L1.init = 1;

    for (int i = 0; i < L2_NUM_SETS; i++) {
        for (int way = 0; way < L2_ASSOCIATIVITY; way++) {
            /* init all L2 values to 0 */
            L2.lines[i][way].dirty = 0;             // Dirty bit
            L2.lines[i][way].valid = 0;             // Valid bit
            L2.lines[i][way].tag = 0;               // Address tag
            for (int k = 0; k < BLOCK_SIZE; k++) {  // Fill data with 0s
                L2.lines[i][way].block[k] = 0;
            }
        }
    }
    L2.init = 1;

    debug("L1 & L2 caches initiated.");
}

void destroyCache() {
    free(L1.lines);
    L1.init = 0;
}

int indexL1(uint32_t address) { return (address / BLOCK_SIZE) % L1_NUM_BLOCKS; }

int indexL2(uint32_t address) { return (address / BLOCK_SIZE) % L2_NUM_SETS; }

void accessL1(uint32_t address, uint8_t *data, uint32_t mode) {
    uint32_t index, tag, offset, dirty_address;

    if (L1.init == 0) initCache();

    tag = address / L1_SIZE;
    index = indexL1(address);
    offset = address % BLOCK_SIZE;
    CacheLine *l1_line = &L1.lines[index];

    // /* debug */
    // char temp_str[128];
    // sprintf(temp_str, "L1: 0x%06X | Tag: 0x%X, Index: %d, Offset: %d, Mode: %s",
    //         address, tag, index, offset, mode ? "READ" : "WRITE");
    // debug(temp_str);

    /* L1 miss (load from L2, write to L2 if dirty) */
    if (!(l1_line->valid && l1_line->tag == tag)) {
        debug("L1 miss");

        /* If there is already a block in the line (with different tag), 
         * write it back to L2 */
        if ((l1_line->valid) && (l1_line->dirty)) {
            debug("Write dirty block to L2 from L1");
            dirty_address = l1_line->tag * L1_SIZE + index * BLOCK_SIZE;
            accessL2(dirty_address, l1_line->block,
                     MODE_WRITE);  // then write back old block
        }

        /* get block from L2 */
        debug("Get block from L2");
        accessL2(address, l1_line->block, MODE_READ);
        l1_line->valid = 1;
        l1_line->tag = tag;
        l1_line->dirty = 0;
    }

    // Read data from cache line
    if (mode == MODE_READ) {
        memcpy(data, &l1_line->block[WORD_SIZE * (offset / WORD_SIZE)],
               WORD_SIZE);
        time += L1_READ_TIME;
    }

    // Write data to cache line
    if (mode == MODE_WRITE) {
        memcpy(&l1_line->block[WORD_SIZE * (offset / WORD_SIZE)], data,
               WORD_SIZE);
        l1_line->dirty = 1;
        time += L1_WRITE_TIME;
    }
}

void accessL2(uint32_t address, uint8_t *data, uint32_t mode) {
    uint32_t index, tag, dram_address, offset, dirty_address;
    uint8_t hit = 0;
    uint32_t temp_time = -1;  // maximum time possible
    int way, way_invalid = -1, way_lru = -1;

    if (L2.init == 0) initCache();

    tag = address / (L2_SIZE / L2_ASSOCIATIVITY);
    index = indexL2(address);
    offset = address % BLOCK_SIZE;
    dram_address = address - offset;
    AssociativeCacheLine *l2_line;

    // /* debug */
    // char temp_str[128];
    // sprintf(temp_str,
    //         "L2: 0x%06X | Tag: 0x%X, Index: %d, Offset: %d, MemAddress: 0x%X, "
    //         "Mode: %s",
    //         address, tag, index, offset, dram_address, mode ? "READ" : "WRITE");
    // debug(temp_str);

    for (way = 0; way < L2_ASSOCIATIVITY; way++) {
        if (L2.lines[index][way].valid && L2.lines[index][way].tag == tag) {
            hit = 1;
            break;
        } else if (!L2.lines[index][way].valid) {
            if (way_invalid < 0)
                way_invalid = way;  // keep the first non-valid way
                                    // to replace if miss
        } else if (temp_time > L2.lines[index][way].time) {
            temp_time = L2.lines[index][way].time;
            way_lru = way;  // keep the LRU way
        }
    }

    if (!hit) {             // L2 miss 
        debug("L2 miss");

        way = (way_invalid < 0)
                  ? way_lru
                  : way_invalid;  // if no invalid way is found, use the LRU way
        l2_line = &L2.lines[index][way];

        // /* debug */
        // sprintf(temp_str, "L2 miss | Replacing way %d - Valid: %d, Time: %d",
        //         way, l2_line->valid, l2_line->time);
        // debug(temp_str);

        if ((l2_line->valid) && (l2_line->dirty)) {  // line has dirty block
            debug("L2 block dirty");

            dirty_address = l2_line->tag * (L2_SIZE / L2_ASSOCIATIVITY) +
                            index * BLOCK_SIZE;
            accessDRAM(dirty_address, l2_line->block,
                       MODE_WRITE);  // then write back old block
        }
        accessDRAM(dram_address, l2_line->block,
                   MODE_READ);  // get new block from DRAM
        l2_line->valid = 1;
        l2_line->tag = tag;
        l2_line->dirty = 0;
    }

    l2_line = &L2.lines[index][way];

    // read data from cache line
    if (mode == MODE_READ) {  
        memcpy(data, &l2_line->block[WORD_SIZE * (offset / WORD_SIZE)], BLOCK_SIZE);
        time += L2_READ_TIME;
    }

    // write data to cache line
    if (mode == MODE_WRITE) {  
        memcpy(&l2_line->block[WORD_SIZE * (offset / WORD_SIZE)], data, BLOCK_SIZE);
        time += L2_WRITE_TIME;
        l2_line->dirty = 1;
    }
    l2_line->time = time;
}

/*********** Interfaces ***********/

void read(uint32_t address, uint8_t *data) {
    accessL1(address, data, MODE_READ);
}

void write(uint32_t address, uint8_t *data) {
    accessL1(address, data, MODE_WRITE);
}

/*********** Debugging ***********/

void debug(char *msg) {
    if (DEBUG) {
        printf("DEBUG: %s\n", msg);
    }
}

/** Prints L1 contents (only non-empty words) */
void printL1() {
    uint8_t print = 0;
    printf("\n----L1----\n");
    for (int i = 0; i < L1_NUM_BLOCKS; i++) {  // for each index
        if (L1.lines[i].valid) {
            printf("Index: %d, Tag: 0x%06X, Dirty: %d\n", i, L1.lines[i].tag,
                   L1.lines[i].dirty);
        }
        for (int k = 0; k < BLOCK_SIZE; k += WORD_SIZE) {  // for each word
            for (int j = 0; j < WORD_SIZE;
                 j++) {  // for each byte, check if any is non-null
                if (L1.lines[i].block[k + j]) {
                    print = 1;
                }
            }
            if (print) {  // if so, print the word
                for (int l = 0; l < WORD_SIZE; l++) {
                    printf("0x%X | %d\n", i * BLOCK_SIZE + k + l,
                           L1.lines[i].block[k + l]);
                }
            }

            print = 0;
        }
    }
    printf("----------\n");
}

/** Prints L2 contents (only non-empty words) */
void printL2() {
    uint8_t print = 0;
    printf("\n----L2----\n");
    for (int i = 0; i < L2_NUM_SETS; i++) {  // for each index
        for (int way = 0; way < L2_ASSOCIATIVITY; way++) {
            if (L2.lines[i][way].valid) {
                printf("Index: %d, Way:%d, Tag: 0x%06X, Dirty: %d, Time:%d\n",
                       i, way, L2.lines[i][way].tag, L2.lines[i][way].dirty,
                       L2.lines[i][way].time);
            }
            for (int word = 0; word < BLOCK_SIZE;
                 word += WORD_SIZE) {  // for each word
                for (int b = 0; b < WORD_SIZE;
                     b++) {  // for each byte, check if any is non-null
                    if (L2.lines[i][way].block[word + b]) {
                        print = 1;
                    }
                }
                if (print) {  // if so, print the word
                    for (int l = 0; l < WORD_SIZE; l++) {
                        printf("0x%X | %d\n", i * BLOCK_SIZE + word + l,
                               L2.lines[i][way].block[word + l]);
                    }
                }

                print = 0;
            }
        }
    }
    printf("----------\n");
}

/** Prints DRAM contents */
void printDRAM() {
    printf("\n---DRAM---\n");
    for (int i = 0; i < DRAM_SIZE; i++) {
        if (DRAM[i]) {
            printf("0x%X | %d\n", i, DRAM[i]);
        }
    }
    printf("----------\n");
}

/** Prints all memory */
void printAll() {
    printL1();
    printL2();
    printDRAM();
}