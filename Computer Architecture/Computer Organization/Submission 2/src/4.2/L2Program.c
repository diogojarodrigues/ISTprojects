#include "../4.2/L2Cache.h"

void test1() {
    uint8_t a[4], b[4] = {1, 2, 3, 4}, c[4] = {5, 6, 7, 8},
                  d[4] = {9, 10, 11, 12};
    debug("BEGINNING");
    printAll();

    // 1010 0000 0000 0000
    debug("\nwrite(0x0000, b);");
    write(0x0000, b);
    printAll();

    debug("\nwrite(0x600, d);");
    write(0x600, d);
    printAll();

    debug("\nwrite(0x8000, c);");
    write(0x8000, c);
    printAll();

    debug("\nwrite(0x0000, b);");
    write(0x0000, b);
    printAll();

    debug("\nread(0x0002, a);");
    read(0x0002, a);
    printf("\na = %d %d %d %d", a[0], a[1], a[2], a[3]);
    printAll();
}

int main(int argc, char **argv) {
    (void)argc;
    (void)argv;

    initCache();

    /* TESTS */

    test1();

    destroyCache();
    return 0;
}