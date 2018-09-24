import { ResponseType, TagType } from "./enums";

export interface KNfcAcr122U {
    connect(
        callbackSuccess: (res: NfcDeviceConnectionReponse | NfcTagReponse) => void,
        callbackError: (err: NfcDeviceConnectionReponse | NfcTagReponse | any) => void): void;

    disconnect(
        callbackSuccess: (res: NfcDeviceConnectionReponse) => void,
        callbackError: (err: any) => void): void;

}

export interface NfcDeviceInfo {
    class: number;
    location: string;
    name: string;
    productId: number;
    vendorId: number;
}

export interface NfcTagInfo {
    tagType: TagType;
    uid: string;
}

export interface NfcDeviceConnectionReponse {
    type: ResponseType;
    message: string;
    deviceInfo: NfcDeviceInfo
}

export interface NfcTagReponse {
    type: ResponseType;
    message: string;
    tagInfo: NfcTagInfo
}



export interface MarchentCredential {
    userId: string;
    pin: string;
}

export interface PaymentInfo {
    amount: string;
    mobileNumber: string;
    invoiceNumber: string;
    email?: string;
    notes?: string;
    extraNote1?: string;
    extraNote2?: string;
    extraNote3?: string;
    extraNote4?: string;
    extraNote5?: string;
    extraNote6?: string;
    extraNote7?: string;
    extraNote8?: string;
    extraNote9?: string;
    extraNote10?: string;
}

/**
 * @property date: Date format should be like YYYY-MM-DD (2018-12-29)
 * @export
 * @interface VoidTransactionInfo
 */
export interface VoidTransactionInfo {
    amount: string;
    date: string;
    cardLast4Digits: string;
}

export interface TransactionInfo {
    bankName: string;
    merchantName: string;
    merchantAdd: string;
    dateTime: string;
    mId: string;
    tId: string;
    batchNo: string;
    voucherNo: string;
    refNo: string;
    saleType: string;
    cardNo: string;
    cardLast4Digits: string;
    trxType: string;
    cardType: string;
    expDate: string;
    emvSigExpDate: string;
    cardHolderName: string;
    currency: string;
    cashAmount: string;
    baseAmount: string;
    tipAmount: string;
    totalAmount: string;
    authCode: string;
    rrNo: string;
    certif: string;
    appId: string;
    appName: string;
    tvr: string;
    tsi: string;
    appVersion: string;
    isPinVarifed: string;
    stan: string;
    cardIssuer: string;
    emiPerMonthAmount: string;
    total_Pay_Amount: string;
    noOfEmi: string;
    interestRate: string;
    billNo: string;
    firstDigitsOfCard: string;
    isConvenceFeeEnable: string;
    invoiceNo: string;
    trxDate: string;
    trxImgDate: string;
    chequeDate: string;
    chequeNo: string;
}