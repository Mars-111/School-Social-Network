

export function instantStringToDate(instantString: string): Date {
    // Parse the instant string to a Date object
    
    const fixed: string = instantString.replace(/(\.\d{3})\d+Z$/, "$1Z");

    const date = new Date(fixed);
    if (isNaN(date.getTime())) {
        throw new Error("Invalid date string");
    }
    return date;
}