# diwhy-webshop.android
# Mobil alkalmazásfejlesztés kötprog

Ez a kötprog egy webshop, amire beregisztrálva megtekinthetjük a termékeket, ezekre a CRUD műveleteket végrehajthatjuk. **Minden felhasználó "admin jogosultságú"**, szóval nincs jogosultságbeli különbség a felhazsnálók közt.  

Ettől függetlenül az odalhoz tartozik egy **"admin"** felhasználó, akinek a belépési adatai a következőek:
 - **email**: admin@gmail.com
 - **pass**: admin123

Az alkalmazásban az utolsó pillanatban ki lettek comment-elve az adatfeltöltő, adatmódosító és adattörlő spinner-ek, mert az OnSuccessListener-ök nem futnak le, annak ellenére, hogy az esemény sikeresen megtörténik. Ez a hiba a beadás napján merült fel, lehet köze van az új Firebase release-nek, hiszen eddig teljesen jól működtek.

Ha leosztályoztad és esetleg el akarod küldeni a pontszámot, akkor itt a Discord elérhetőségem:
 - Darcosi#1867

**APK:** diwhy-webshop.apk
