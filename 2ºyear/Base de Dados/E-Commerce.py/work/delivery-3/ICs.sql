-- IC-1
ALTER TABLE employee 
ADD CHECK (EXTRACT(YEAR FROM AGE(CURRENT_DATE, bdate)) >= 18);


-- IC-2
CREATE OR REPLACE FUNCTION check_workplace_exclusivity()
RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT COUNT(*) FROM office WHERE address = NEW.address)
        + (SELECT COUNT(*) FROM warehouse WHERE address = NEW.address) != 1 THEN
        RAISE EXCEPTION 'Address must be in warehouse or office but not both';
    END IF;
    
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER workplace_exclusivity
AFTER INSERT ON workplace
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION check_workplace_exclusivity();

CREATE CONSTRAINT TRIGGER workplace_exclusivity
AFTER INSERT OR UPDATE OR DELETE ON office
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION check_workplace_exclusivity();

CREATE CONSTRAINT TRIGGER workplace_exclusivity
AFTER INSERT OR UPDATE OR DELETE ON warehouse
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION check_workplace_exclusivity();


-- IC-3
CREATE OR REPLACE FUNCTION check_order_in_contains()
RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT COUNT(*) FROM contains WHERE order_no = NEW.order_no) = 0 THEN
        RAISE EXCEPTION 'Order_no must exist in contains';
    END IF;
    
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION check_order_still_in_contains()
RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT COUNT(*) FROM contains WHERE order_no = OLD.order_no) = 0
        AND EXISTS(SELECT order_no FROM orders WHERE order_no = OLD.order_no) THEN
        RAISE EXCEPTION 'Order_no must exist in contains';
    END IF;
    
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER check_order_in_contains
AFTER INSERT ON orders
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION check_order_in_contains();

CREATE CONSTRAINT TRIGGER check_order_still_in_contains
AFTER UPDATE OR DELETE ON contains
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION check_order_still_in_contains();